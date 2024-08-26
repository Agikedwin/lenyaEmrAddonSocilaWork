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
@Builds({ "kenyaemr.coptic.module.report.mfnurse" })

public class FUNurseFormReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("Medical follow up Nurse Report");
        dsd.setDescription("Medical follow up Nurse Report Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("use openmrs;\n" +
                "select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as Patient_name,\n" +
                "\te.patient_id,\n" +
                "\te.location_id,\n" +
                "\tdate(e.encounter_datetime) as visit_date,\n" +
                "\te.creator as encounter_provider,\n" +
                "    max(if(o.concept_id=165196,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as Did_you_refer_client_for_CxCx_Screening,\n" +
                "    max(if(o.concept_id=163589,(case o.value_coded when 163291 then \"Client has never been sexually active\" when 159837 then \"Client has had total hysterectomy\" when 165225 then \"No service available at this time\" when 161504 then \"Patient had screening test in last year\" when 1185 then \"Client currently on Cxcx treatment follow up\" when 164146 then \"Client is less than 10wks post partum\" when 162570 then \"Client declined\" when 159778 then \"Client has myth/fear\" when 5622 then \"Other\" else \"\" end),null)) as If_No_Why_is_the_client_not_referred,\n" +
                "    max(if(o.concept_id=160303,o.value_text,null)) as Notes_Summary,\n" +
                "    \n" +
                "    \n" +
                "\t\n" +
                "\n" +
                "     \te.date_created as date_created,\n" +
                "\n" +
                "    e.voided as voided\n" +
                "from openmrs.encounter e\n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.person_name pn on pn.person_id=e.patient_id and p.voided=0\n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('8df18cf0-3d49-4c2e-9f29-f7e650353b4e')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (165196,163589,160303)\n" +
                "where  e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0 \n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}