package edu.internet2.middleware.grouper.ws.rest.provider;

import edu.internet2.middleware.grouper.ws.rest.CustomGrouperRestProvider;
import edu.internet2.middleware.grouper.ws.rest.CustomGrouperRestRequest;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import org.apache.commons.lang3.tuple.Pair;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MembershipCountRestProvider implements CustomGrouperRestProvider {
    private static final Map<String, String> ALLOWED_COLUMS = new HashMap();
    static {
        ALLOWED_COLUMS.put("IMMEDIATE_MSHIP_ENABLED", "gms.IMMEDIATE_MSHIP_ENABLED");
        ALLOWED_COLUMS.put("TYPE", "gfl.TYPE");
        ALLOWED_COLUMS.put("NAME", "gfl.NAME");
        ALLOWED_COLUMS.put("SUBJECT_SOURCE", "gm.SUBJECT_SOURCE");
        ALLOWED_COLUMS.put("MSHIP_TYPE", "gms.MSHIP_TYPE");
    }

    @Override
    public boolean supports(CustomGrouperRestRequest o) {
        return "membershipCount".equals(o.getUrlStrings().get(o.getUrlStrings().size() - 1));
    }

    @Override
    public Object provide(CustomGrouperRestRequest o) {
        String groupName = URLDecoder.decode(o.getUrlStrings().get(2));

        final Map<String, String[]> defaults = new HashMap<>();
        defaults.put("IMMEDIATE_MSHIP_ENABLED", new String[]{"T"});
        defaults.put("TYPE", new String[]{"list"});
        defaults.put("NAME", new String[]{"members"});
        defaults.putAll(o.getParameterMap());

        //TODO: handle not

        Pair<String, List<String>> queryBindVars = buildQuery(groupName,defaults);
        if (!defaults.containsKey("MSHIP_TYPE")) {
            // no membership type sent, we want to do both types
            int effectiveCount = getCount(queryBindVars.getLeft(), queryBindVars.getRight().toArray(new String[]{}));

            String immediateQuery = queryBindVars.getLeft() + " and gms.MSHIP_TYPE = 'immediate'";
            int immediateCount = getCount(immediateQuery, queryBindVars.getRight().toArray(new String[]{}));

            return new MembershipCountRestProviderResponse(immediateCount, effectiveCount, null);
        } else {
            return new MembershipCountRestProviderResponse(null, null, getCount(queryBindVars.getLeft(), queryBindVars.getRight().toArray(new String[]{})));
        }
    }

    private Pair<String, List<String>> buildQuery(String groupName, Map<String, String[]> params) {
        StringBuilder query = new StringBuilder();
        List<String> bindVars = new ArrayList<>();
        bindVars.add(groupName);

        query.append("select count(distinct gm.SUBJECT_ID)" +
                "from grouper_memberships_all_v gms," +
                "     grouper_members gm," +
                "     grouper_groups gg," +
                "     grouper_fields gfl" +
                " where gg.name = ?" +
                "  and gms.OWNER_GROUP_ID = gg.id" +
                "  and gms.FIELD_ID = gfl.ID" +
                "  and gms.MEMBER_ID = gm.ID");

        params.entrySet().stream().forEach(e -> {
                    String eKey;
                    boolean not = false;
                    if (e.getKey().startsWith("!")) {
                        // if the key startes with !, this is a not
                        not = true;
                        eKey = e.getKey().substring(1);
                    } else {
                        eKey = e.getKey();
                    }
                    if (ALLOWED_COLUMS.containsKey(eKey)) {
                        // we're allowed to use this key
                        query.append(" and");
                        query.append(" ");
                        query.append(ALLOWED_COLUMS.get(eKey));
                        if (e.getValue().length > 1) {
                            // use in
                            if (not) {
                                query.append(" not");
                            }
                            query.append(" in");
                            query.append(" ");
                            query.append(GcDbAccess.createInString(e.getValue().length));
                        } else {
                            // use =
                            if (not) {
                                query.append(" <>");
                            } else {
                                query.append(" =");
                            }
                            query.append(" ?");
                        }
                        Collections.addAll(bindVars, Arrays.stream(e.getValue()).map(i -> URLDecoder.decode(i)).toArray(String[]::new));
                    }
                }
        );

        return Pair.of(query.toString(), bindVars);
    }

    private int getCount(String query, String... bindVars) {
        return new GcDbAccess().sql(query).bindVars(bindVars).selectList(Integer.class).get(0);
    }
}
