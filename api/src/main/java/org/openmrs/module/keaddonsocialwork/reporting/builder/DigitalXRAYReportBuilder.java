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
@Builds({ "kenyaemr.coptic.module.report.digitalXRay" })
public class DigitalXRAYReportBuilder extends AbstractReportBuilder {

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
        dsd.setName("digitalXRay");
        dsd.setDescription("Digital X-RAY Report");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("select\n" +
                "                concat_ws(\"   \", pn.given_name, pn.family_name) as \"CLIENT NAME\",\n" +
                "                e.patient_id as 'Patient ID',\n" +
                "                e.location_id as 'Location ID',\n" +
                "                    max(if(o.concept_id=12,(case o.value_coded \n" +
                "                    when 160015 then \"Suggestive of TB \"\n" +
                "                    when 114100 then \"Suggestive of Pneumonia \"\n" +
                "                    when 161297 then \"Normal Chest X-Ray \"\n" +
                "                    when 165649 then \"Inconclusive\" else \"\" end),null)) as \"X-RAY Results\",\n" +
                "                    max(if(o.concept_id=1000031,(case o.value_coded \n" +
                "                    when 1065 then \"Yes\" \n" +
                "                    when 1066 then \"No\" else \"\"  end),null)) as \"Referral Client\",    \n" +
                "                     max(if(o.concept_id=161550,o.value_text,null)) as \"Facility Name\",\n" +
                "                      max(if(o.concept_id=165416,o.value_text,null)) as \"MFL Code\",\n" +
                "                       max(if(o.concept_id=168130,o.value_text,null)) as \"Email Address\",\n" +
                "\t\t\t\t\tdate(e.encounter_datetime) as 'Visit Date',\n" +
                "\t\t\t\t\tconcat_ws(\" \",pnn.given_name, pnn.family_name) as \"Provider\"\n" +
                "                \n" +
                "                from encounter e\n" +
                "                inner join person p on p.person_id=e.patient_id and p.voided=0\n" +
                "                inner join openmrs.person_name pn on pn.person_id=e.patient_id and p.voided=0\n" +
                "\t\t\t\tinner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\t\t\t\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "\t\t\t\tinner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "                inner join(select form_id from form where uuid in('a298d515-e3cb-47e7-a5be-288cb603576c')\n" +
                "                ) f on f.form_id=e.form_id\n" +
                "                left outer join obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "                and o.concept_id in (160015,1000031,161550,165416,168130)\n" +
                "                 where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "                group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}