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
@Builds({ "kenyaemr.coptic.module.report.dreamsOvc" })
public class DreamOVCGirls extends AbstractReportBuilder {

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
        dsd.setName("dreamsOvc");
        dsd.setDescription("OVC DREAM GIRLS 10-26");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as \"CLIENT NAME\",\n" +
                "\te.patient_id as PID,\n" +
                "\te.location_id as LID,\n" +
                "\tdate(e.encounter_datetime) as \"VISIT DATE\",\n" +
                "\te.creator as \"ENCOUNTER PROVIDER\",\n" +
                "    \n" +
                "    max(if(o.concept_id=162699,(case o.value_coded when 152292 then \"Pregnant\" when 127910 then \"Breastfeeding\" when 140790 then \"Experincing consistent sGBV\" when 138447 then \"Dropped out of School\" when 126302 then \"Engaging in unprotected sex/transactional sex\" when 118686 then \"Alcohol misuse\" else \"\" end),null)) as \"ELIGIBILITY CRITERIA\",\n" +
                "    max(if(o.concept_id=160433,(case o.value_coded when 664 then \"Client Unwilling to Enrol\" when 703 then \"Accepted to enroll\" when 1065 then \"No DREAMS Program in county of residence\" when 1118 then \"Already Enrolled to DREAMS\" else \"\" end),null)) as \"SCREENING OUTCOME\",\n" +
                "    max(if(o.concept_id=162166,o.value_text,null)) as \"IF DECLINED KINDLY GIVE REASONS\",\n" +
                "    max(if(o.concept_id=162166,o.value_text,null)) as \"PROGRAM REFFERED TO\",\n" +
                "    max(if(o.concept_id=5096,o.value_datetime,null)) as \"DATE CLIENT PREFERS TO ENROL FOR DREAMS\",\n" +
                "    max(if(o.concept_id=165144,o.value_datetime,null)) as \"DATE CONFIRMED LINKAGE TO DREAMS PROGRAM\",\n" +
                "\tconcat_ws(\" \",pnn.given_name, pnn.family_name) as \"PROVIDER NAME\",\n" +
                "     \te.date_created as \"DATE CREATED\"\n" +
                "from openmrs.encounter e\n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "\t\n" +
                "    inner join openmrs.person_name pn on pn.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "    inner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "    \n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('51281515-9b8c-4d5e-977f-430ec6fc3178')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (162699,160433,162166,162166,5096,165144)\n" +
                "   where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0 \n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}