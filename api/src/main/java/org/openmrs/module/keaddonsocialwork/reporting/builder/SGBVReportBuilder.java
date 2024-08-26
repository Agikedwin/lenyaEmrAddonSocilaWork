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
@Builds({"kenyaemr.coptic.module.report.sgbv" })

public class SGBVReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("sgbv");
        dsd.setDescription("SGBV Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("\n" +
                "select\n" +
                "    concat_ws(\" \",pn.given_name, pn.family_name) as \"CLIENT NAME\",\n" +
                "\te.patient_id as \"PID\",\n" +
                "\te.location_id as \"LID\",\n" +
                "\tdate(e.encounter_datetime) as \"VISIT DATE\",\n" +
                "    concat_ws(\" \",pnn.given_name, pnn.family_name) as \"Provider Name\",\n" +
                "    max(if(o.concept_id=165349,o.obs_datetime,null)) as \"SGBV Date\",\n" +
                "    max(if(o.concept_id=1521,o.obs_datetime,null)) as \"Violence Time\",\n" +
                "    CONCAT_WS(\"   \",max(if(o.concept_id=165205,(case o.value_coded when 152292 then \"Sexual Assault\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 152292 then \"Sexual Assault\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 127910 then \"Rape\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 140790 then \"Attempted Rape\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 138447 then \"Sodomy\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 126302 then \"Intimate partner\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 148143 then \"Attempted Defilement\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 118686 then \"Defilement\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 139108 then \"Sexual harassment\"  else \"\" end),null)),\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 163065 then \"Indecent Act\"  else \"\" end),null))\n" +
                "    \n" +
                "    ) as  \"Violence Type\",\n" +
                "    max(if(o.concept_id=163488,(case o.value_coded when 160542 then \"OPD\" when 167050 then \"IPD\" when  159372 then \"CCC\" when 5622 then \"Others\" else if(o.concept_id=163530,o.value_text,null) end),null)) as \"Service Delivery Point\",\n" +
                "    coalesce( max(if(o.concept_id=162165,o.value_text,null)), max(if(o.concept_id=163530,o.value_text,null)),\n" +
                "    max(if(o.concept_id=164803,o.value_text,null)), max(if(o.concept_id=163530,o.value_text,null))) as Client_Number,\n" +
                "    max(if(o.concept_id=159427,(case o.value_coded when 664 then \"Negative\" when 664 then \"Negative\"  when 703 then \"Positive\" when 1065 then \"Known Positive\" when 1118 then \"Not done\" when 1138 then \"Not Eligible\"  else \"\" end),null)) as \"Test Results\",\n" +
                "\tmax(if(o.concept_id=165093,(case o.value_coded when 123154 then \"Trauma\" when 166536 then \"Given PEP within 72 hrs\"  \n" +
                "    when 60570 then \"Given ECP within 120 hrs\" when 5490 then \"Psychosocial Support\" when 160551 then \"Police referral\" \n" +
                "    when 118668 then \"STI Prophylaxis\"  when 5622 then if(o.concept_id=165145, o.value_text,null) else \"\" end),null)) as \"Services Offered\",\n" +
                "    max(if(o.concept_id=165229,(case o.value_coded when 166051 then \"Known\" when 1067 then \"Unknown\" else \"\" end),null)) as \"Perpetrators\",\n" +
                "    max(if(o.concept_id=163541,(case o.value_coded when 163563 then \"Less than 18 yrs\" when 163562 then \"18-24 yrs\"\n" +
                "    when \"163332\" then \"25-34\"  when 1734 then \"35-49 yrs\" when 163328 then \"50 yrs and above\" else \"\" end),null)) as \"Perpetrators Age\",\n" +
                "\n" +
                "    max(if(o.concept_id=1533,(case o.value_coded when 1534 then \"Male\" when 1535 then \"Female\" else \"\" end),null)) as \"Perpetrators Gender\",\n" +
                "    max(if(o.concept_id = 5096, o.value_datetime,null)) as \"Next Appointment\"\n" +
                "    \n" +
                "from openmrs.encounter e \n" +
                "\tinner join openmrs.person_name pn on pn.person_id=e.patient_id and pn.voided=0\n" +
                "    inner join openmrs.patient_identifier pi  on pi.patient_id=e.patient_id and pn.voided=0 \n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "\tinner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "\tinner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "    \n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('5f48c23d-a859-4446-86c7-4282b0516559')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (165349,165205,163530,162165,1521,163530,164803,163530,163488,164803,163530,159427,165093,165145,5622,165229,\n" +
                "     163541,1533,5096,162166)\n" +
                " where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}