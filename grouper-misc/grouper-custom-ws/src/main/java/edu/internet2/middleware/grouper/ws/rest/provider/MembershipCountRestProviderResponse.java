package edu.internet2.middleware.grouper.ws.rest.provider;

public class MembershipCountRestProviderResponse {
    private final Integer immediate;
    private final Integer effective;
    private final Integer count;

    public MembershipCountRestProviderResponse(Integer immediate, Integer effective, Integer count) {
        this.immediate = immediate;
        this.effective = effective;
        this.count = count;
    }

    public Integer getImmediate() {
        return immediate;
    }

    public Integer getEffective() {
        return effective;
    }

    public Integer getCount() {
        return this.count;
    }
}
