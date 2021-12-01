package edu.internet2.middleware.grouper.ws.rest.provider;

import edu.internet2.middleware.grouper.ws.rest.CustomGrouperRestProvider;
import edu.internet2.middleware.grouper.ws.rest.CustomGrouperRestRequest;
import edu.internet2.middleware.grouperClient.jdbc.GcDbAccess;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MembershipCountRestProvider implements CustomGrouperRestProvider {
    private static final Map<String, DbColumn> ALLOWED_COLUMNS = new HashMap();
    static {
        ALLOWED_COLUMNS.put("IMMEDIATE_MSHIP_ENABLED", new DbColumn("IMMEDIATE_MSHIP_ENABLED", "gms.IMMEDIATE_MSHIP_ENABLED", String.class));
        ALLOWED_COLUMNS.put("TYPE", new DbColumn("TYPE", "gfl.TYPE", String.class));
        ALLOWED_COLUMNS.put("NAME", new DbColumn("NAME", "gfl.NAME", String.class));
        ALLOWED_COLUMNS.put("SUBJECT_SOURCE", new DbColumn("SUBJECT_SOURCE", "gm.SUBJECT_SOURCE", String.class));
        ALLOWED_COLUMNS.put("MSHIP_TYPE", new DbColumn("MSHIP_TYPE", "gms.MSHIP_TYPE", String.class));
        ALLOWED_COLUMNS.put("ENABLED_TIME", new DbColumn("ENABLED_TIME", "gms.immediate_mship_enabled_time", BigInteger.class));
        ALLOWED_COLUMNS.put("DISABLED_TIME", new DbColumn("DISABLED_TIME", "gms.immediate_mship_disabled_time", BigInteger.class));
    }

    private static final String BASE_QUERY = "select count(distinct gm.SUBJECT_ID)" +
            "from grouper_memberships_all_v gms," +
            "     grouper_members gm," +
            "     grouper_groups gg," +
            "     grouper_fields gfl" +
            " where gg.name = ?" +
            "  and gms.OWNER_GROUP_ID = gg.id" +
            "  and gms.FIELD_ID = gfl.ID" +
            "  and gms.MEMBER_ID = gm.ID";

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

        Pair<String, List<Object>> queryBindVars = buildQuery(groupName,defaults);
        if (!defaults.containsKey("MSHIP_TYPE")) {
            // no membership type sent, we want to do both types
            int effectiveCount = getCount(queryBindVars.getLeft(), queryBindVars.getRight().toArray());

            String immediateQuery = queryBindVars.getLeft() + " and gms.MSHIP_TYPE = 'immediate'";
            int immediateCount = getCount(immediateQuery, queryBindVars.getRight().toArray());

            return new MembershipCountRestProviderResponse(immediateCount, effectiveCount, null);
        } else {
            return new MembershipCountRestProviderResponse(null, null, getCount(queryBindVars.getLeft(), queryBindVars.getRight().toArray()));
        }
    }

    protected Pair<String, List<Object>> buildQuery(String groupName, Map<String, String[]> params) {
        StringBuilder query = new StringBuilder();
        List<Object> bindVars = new ArrayList<>();
        bindVars.add(groupName);

        query.append(BASE_QUERY);

        params.entrySet().stream().forEach(e -> {
                    String eKey;
                    boolean isNot = false;
                    String operator = null;

                    Pattern pattern = Pattern.compile("(.+)?([<>]=?)(.+)?");
                    Matcher matcher = pattern.matcher(e.getKey());

                    if (e.getKey().startsWith("!") || e.getKey().endsWith("!")) {
                        // if the key starts or ends with !, this is a not
                        isNot = true;
                        eKey = e.getKey().replaceFirst("!", "");
                    } else if(matcher.matches()) {
                        eKey = e.getKey().replaceFirst("[<>]=?", "");
                        operator = matcher.group(2);
                    } else {
                        eKey = e.getKey();
                    }
                    if (ALLOWED_COLUMNS.containsKey(eKey)) {
                        // we're allowed to use this key
                        query.append(" and");
                        query.append(" ");
                        query.append(ALLOWED_COLUMNS.get(eKey).getColumnName());
                        if (e.getValue().length == 1 && e.getValue()[0].equals("NULL")) {
                            query.append(" is");
                            if (isNot) {
                                query.append(" not");
                            }
                            query.append(" NULL");
                        } else if(operator != null) {
                            query.append(" ");
                            query.append(operator);
                            query.append(" ?");
                            bindVars.add(convertType(URLDecoder.decode(e.getValue()[0]), ALLOWED_COLUMNS.get(eKey).getType()));

                        } else {
                            if (e.getValue().length > 1) {
                                // use in
                                if (isNot) {
                                    query.append(" not");
                                }
                                query.append(" in");
                                query.append(" ");
                                query.append(GcDbAccess.createInString(e.getValue().length));
                            } else {
                                // use =
                                if (isNot) {
                                    query.append(" <>");
                                } else {
                                    query.append(" =");
                                }
                                query.append(" ?");
                            }
                            Collections.addAll(bindVars, Arrays.stream(e.getValue()).map(i -> convertType(URLDecoder.decode(i), ALLOWED_COLUMNS.get(eKey).getType())).toArray());
                        }
                    }
                }
        );

        return Pair.of(query.toString(), bindVars);
    }

    private <T>T convertType(String value, Class<T> type) {
        if (type.equals(String.class)) {
            return type.cast(value);
        } else if (type.equals(BigInteger.class)) {
            return (T) new BigInteger(value);
        }
        return null;
    }

    private int getCount(String query, Object... bindVars) {
        return new GcDbAccess().sql(query).bindVars(bindVars).selectList(Integer.class).get(0);
    }
}
