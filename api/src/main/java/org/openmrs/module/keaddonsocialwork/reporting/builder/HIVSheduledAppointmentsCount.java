package org.openmrs.module.keaddonsocialwork.reporting.builder;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.coptic.module.report.appointmentCount" })

public class HIVSheduledAppointmentsCount extends  AbstractReportBuilder {

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        return Arrays.asList(ReportUtils.map(reportColumns(), "startDate=${startDate},endDate=${endDate}"));
    }

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
                Date.class), new Parameter("dateBasedReporting", "", String.class));
    }

    /**
     * Columns for the report
     *
     * @return
     */
    protected DataSetDefinition reportColumns() {
        SqlDataSetDefinition dsd = new SqlDataSetDefinition();
        dsd.setName("Sheduled Appointments Count");
        dsd.setDescription("Clients Appointments Distribution");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("SELECT 'HIV Consultaion' as 'Appointment Type', tca as 'Appointment date', \n" +
                "case when day_week = 0 then 'Monday' when day_week = 1 then 'Tuesday'\n" +
                "when day_week = 2 then 'Wednesday' when day_week = 3 then 'Thursday'\n" +
                "when day_week = 4 then 'Friday' when day_week = 5 then 'Saturday'\n" +
                "when day_week = 6 then 'Sunday' end as weekday, tca_due as 'Clients Booked',\n" +
                "130 as 'Expected Number', (130 - tca_due) as 'Variance'\n" +
                "FROM\n" +
                "(SELECT tca,weekday(tca) as day_week, count(tca) as tca_due \n" +
                "FROM\n" +
                "(SELECT patient_id, date(date_appointment_scheduled) as visit_date,date(start_date_time) as tca\n" +
                "FROM openmrs.patient_appointment\n" +
                "WHERE appointment_service_id = 1 \n" +
                "GROUP BY patient_id,date(date_appointment_scheduled)\n" +
                "HAVING tca >= date(now()))x\n" +
                "GROUP BY tca)xy");

        return dsd;

    }

}