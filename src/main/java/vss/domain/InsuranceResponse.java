package vss.domain;

public class InsuranceResponse {
    private InsuranceReport report;

    public InsuranceResponse(InsuranceReport report) {
        this.report = report;
    }
    public InsuranceReport getReport() {
        return report;
    }
}
