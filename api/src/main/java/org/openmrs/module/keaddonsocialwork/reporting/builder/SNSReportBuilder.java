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
@Builds({"kenyaemr.coptic.module.report.sns" })

public class SNSReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("sns");
        dsd.setDescription("SNS Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("select\t\n" +
                "    concat_ws(\" \",pn.given_name, pn.family_name) as \"Client name\",\n" +
                "\te.patient_id,\n" +
                "\te.location_id,\n" +
                "\tdate(e.encounter_datetime) as \"VISIT DATE\",\n" +
                "\te.creator as encounter_provider, \n" +
                "    CONCAT_WS(\"   \",max(if(o.concept_id=1169,(case o.value_coded when 138571 then \"KP (Known Positive)\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=1169,(case o.value_coded when 138571 then \"KP (Known Positive)\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=1169,(case o.value_coded when 703 then \"NP (New Positive)\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=1169,(case o.value_coded when 664 then \"NN (New Negative)\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=1169,(case o.value_coded when 692 then \"KN (Known Negative)\"  else \"\" end),null))    \n" +
                "    ) as  \"RECRUITER TYPE\",  \n" +
                "    max(if(o.concept_id=167158,o.value_text,null)) as \"CCC NUMBER OF THE CLIENT\",\n" +
                "\tmax(if(o.concept_id=1646,o.value_text,null)) as \"COUPON ID GIVEN\",\n" +
                "    max(if(o.concept_id=163151,o.value_text,null)) as \"COUPON ID 1\",\n" +
                "\tmax(if(o.concept_id=163149,o.value_text,null)) as \"COUPON ID 2\",\n" +
                "    max(if(o.concept_id=162725,o.value_text,null)) as \"COUPON ID 3\",\n" +
                "    max(if(o.concept_id=163150,o.value_text,null)) as \"COUPON ID 4\",\n" +
                "    max(if(o.concept_id=160716,o.value_text,null)) as \"COUPON ID 5\",\n" +
                "    concat_ws(\" \",pnn.given_name, pnn.family_name) as \"PROVIDER NAME\"\n" +
                "from openmrs.encounter e \n" +
                "\tinner join openmrs.person_name pn on pn.person_id=e.patient_id and pn.voided=0\n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "    inner join openmrs.person_name pnn on pnn.person_id = pv.person_id    \n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('4e8a44e1-333d-415e-932f-97fed7715164')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (1169,167158,1646,163151,163149,162725,163530,163150,160716)\n" +
                "    where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}