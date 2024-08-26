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
@Builds({ "kenyaemr.coptic.module.report.utou" })
public class UtoUReportBuilder extends AbstractReportBuilder {

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
        dsd.setName("utou");
        dsd.setDescription("UtoU Services ");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("select\n" +
                "\t\tconcat_ws(\"   \", pn.given_name, pn.family_name) as \"Name\",\n" +
                "\t\te.patient_id AS PID,\n" +
                "\t\te.location_id AS  LID,\n" +
                "\t\tdate(e.encounter_datetime) as \"Visit Date\",\n" +
                "\t\tmax(if(o.concept_id=1169,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Familiar utou concept\",\n" +
                "\t\tmax(if(o.concept_id=5272,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Know Current Vl\",\n" +
                "\t\tmax(if(o.concept_id=164844,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Adhering Treatment Plan\",\n" +
                "\t\tmax(if(o.concept_id=165205,(case o.value_coded when 118668 then \"Disclosure\" when 158948 then \"Religion\" when 1535 then \"Distance to the Clinic\" when 84336 then \"Work\" when 162198 then \"Other\" when 162932 then \"Herbal Medication\" when 152292 then \"Stigma\"  when 162645 then \"GBV\"  else \"\" end),null)) as \"Adherence Barrier\",\n" +
                "\t\tmax(if(o.concept_id=163787,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Talked to Partner\",\n" +
                "\t\tmax(if(o.concept_id=167161,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Connected to Social Support\",\n" +
                "\t\tmax(if(o.concept_id=141814,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"CCC Number Known\",\n" +
                "\t\tmax(if(o.concept_id=123160,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as \"Knows Drug Name\",\n" +
                "\t\t max(if(o.concept_id=160716,o.value_text,null)) as Comments,\n" +
                "\t\t concat_ws(\" \",pnn.given_name, pnn.family_name) as \"PROVIDER NAME\"\n" +
                "\t\tfrom encounter e\n" +
                "\t\tinner join openmrs.person_name pn on pn.person_id=e.patient_id and pn.voided=0\n" +
                "\t\tinner join openmrs.patient_identifier pi  on pi.patient_id=e.patient_id and pn.voided=0 \n" +
                "\t\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "\t\tinner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\t\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "\t\tinner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "\t\tinner join\n" +
                "\t\t(\n" +
                "\t\tselect form_id from form where\n" +
                "\t\tuuid in('e1b67a74-45b9-43cc-bd54-67a3a2b34f0f')\n" +
                "\t\t) f on f.form_id=e.form_id\n" +
                "\t\tleft outer join obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t\t and o.concept_id in (1169,5272,164844,165205,163787,167161,141814,163258,123160,160716,1711)\n" +
                "\t\t  where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "\t\tgroup by e.patient_id, e.encounter_id;");

        return dsd;

    }

}