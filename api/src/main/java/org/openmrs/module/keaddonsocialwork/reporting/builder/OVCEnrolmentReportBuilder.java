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
@Builds({ "kenyaemr.coptic.module.report.ovc" })

public class OVCEnrolmentReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("OVC Enrolment Report");
        dsd.setDescription("OVC Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as \"Client Name\",\n" +
                "\te.patient_id PID,\n" +
                "\te.location_id LID,\n" +
                "\tdate(e.encounter_datetime) as \"Visit Date\",\n" +
                "    max(if(o.concept_id=162699,(case o.value_coded when 152292 then \"HIV Exposed Infant\" when 127910 then \"Child/Adolescent Living with HIV\" when 140790 then \"Survivor of Gender based violence\" else \"\" end),null)) as \"Eligibility Reason\",\n" +
                "    max(if(o.concept_id=160433,(case o.value_coded when 664 then \"Declined to Enrol\" when 703 then \"Re-screening Accepted to Enroll\" when 1065 then \"No OVC Program in county of residence\" when 1118 then \"Already in OVC program\" else \"\" end),null)) as \"Screening Outcome\",\n" +
                "    max(if(o.concept_id=162166,o.value_text,null)) as \"Decline Reason\",\n" +
                "    max(if(o.concept_id=162166,o.value_text,null)) as \"OVC_program Referred\",\n" +
                "    max(if(o.concept_id=5096,o.value_datetime,null)) as \"Date_Rreferred\",\n" +
                "    max(if(o.concept_id=165144,o.value_datetime,null)) as \"Date Linked\",\n" +
                "    concat_ws(\" \",pnn.given_name, pnn.family_name) as \"PROVIDER NAME\"\n" +
                "from openmrs.encounter e\n" +
                "\tinner join openmrs.person_name pn on pn.person_id=e.patient_id and pn.voided=0\n" +
                "    inner join openmrs.patient_identifier pi  on pi.patient_id=e.patient_id and pn.voided=0 \n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "\tinner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('3515e5ea-6758-4266-8ddd-6848f0b55587')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (162699,160433,162166,162166,5096,165144)\n" +
                "     where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}