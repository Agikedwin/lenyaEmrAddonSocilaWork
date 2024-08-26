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
@Builds({ "kenyaemr.coptic.module.report.pmtcovc" })
public class PmtcOVCIntegration extends AbstractReportBuilder {

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
        dsd.setName("PMTCT OVC");
        dsd.setDescription("PMTCT OVC Integration");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as \"CLIENT NAME\",\n" +
                "\te.patient_id,\n" +
                "\te.location_id,\n" +
                "\tdate(e.encounter_datetime) as visit_date,\n" +
                "    max(if(o.concept_id=160433,(case o.value_coded when 664 then \"HIV positive pregnant adolescents\" when 703 then \"HIV positive pregnant with VL >50 copies/ml\" when 1065 then \"HIV positive breastfeeding mothers and their exposed infants (mother-baby pair) including adolescent mothers\" else \"\" end),null)) as \"CLIENT TYPE\",\n" +
                "    max(if(o.concept_id=162725,o.value_text,null)) as \"HEI NO OF THE CLIENT\",\n" +
                "\tmax(if(o.concept_id=162699,(case o.value_coded when 152292 then \"HIV positive Pregnant Adolescents. Including OVC\" when 127910 then \"Clients with detectable VL>50 copies/ml\" when 140790 then \"Those who drop off ART i.e defaulters/interruption in treatment\" when 138447 then \"Clients with stigma, declining treatment, poor adherence\" when 126302 then \"Client with active comorbidities - DM, OIs, malnourished (low MUAC), mental health etc.\" else \"\" end),null)) as \"ELIGIBILITY REASON CLIENT IS\",\n" +
                "    max(if(o.concept_id=165286,(case o.value_coded when 664 then \"Declined to Enrol\" when 703 then \"Accepted to Enroll\" when 1065 then \"No OVC Program in county of residence\" when 1118 then \"Already in OVC program\" else \"\" end),null)) as \"SCREENING OUTCOMES\",\n" +
                "    max(if(o.concept_id=162166,o.value_text,null)) as \"NAME OF OVC PROGRAM REFFERED TO\",\n" +
                "    max(if(o.concept_id=5096,o.value_datetime,null)) as \"DATE ENROLLED\",\n" +
                "\tmax(if(o.concept_id=1000531,o.value_text,null)) as \"OVC ENROLLMENT NO\",\t\n" +
                "\te.date_created as \"DATE CREATED\",\n" +
                "\tconcat_ws(\" \",pnn.given_name, pnn.family_name) as \"PROVIDER NAME\"\n" +
                "\n" +
                "from openmrs.encounter e\n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.person_name pn on pn.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "    inner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "    \n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('a7bc1eaf-ddbf-4ca4-b213-0694a2f3b01f')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (160433,162725,162699,165286,162166,5096,1000531)\n" +
                "    where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "group by e.patient_id, e.encounter_id;");
        dsd.setSqlQuery("");

        return dsd;

    }

}