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
@Builds({ "kenyaemr.coptic.module.report.udab" })

public class UdabCancerReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("UDAB  Report");
        dsd.setDescription("UDAB Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("\n" +
                "select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as Patient_name,\n" +
                "\te.patient_id,\n" +
                "\te.location_id,\n" +
                "\tdate(e.encounter_datetime) as visit_date,\n" +
                "\te.creator as encounter_provider,\n" +
                "    max(if(o.concept_id=160288,(case o.value_coded when 162080 then \"Initial visit\" when 161236 then \"Routine visit\" when 165381 then \"Post treatment visit\" when 1185 then \"Treatment visit\" when 165382 then \"Post treatment complication\" else \"\" end),null)) as UDAB_Cervical_cancer_screening_form,\n" +
                "    max(if(o.concept_id=164181,(case o.value_coded when 164180 then \"First time screening\" when 160530 then \"Re-screening after previous negative results\" when 165389 then \"Post treatment followup screening\" else \"\" end),null)) as Screening_type,\n" +
                "    max(if(o.concept_id=162816,(case o.value_coded when 162816 then \"Cryotherapy\" when 162810 then \"LEEP\" when 5622 then \"Others\" else \"\" end),null)) as Post_treatment_complication_cause,\n" +
                "    #max(if(o.concept_id=163042,(case o.value_text else \"\" end),null)) as Explain,\n" +
                "    max(if(o.concept_id=163589,(case o.value_coded when 164805 then \"VIA\" when 164977 then \"VIA/VILI\" when 160705 then \"Colposcopy\" when 885 then \"PAP Smear\" when 159859 then \"HPV Test\" else \"\" end),null)) as Screening_method,\n" +
                "    max(if(o.concept_id=164934,(case o.value_coded when 664 then \"Negative\" when 703 then \"Positive\" when 159008 then \"Suspicious for cancer\" when 5622 then \"Other\" else \"\" end),null)) as VIA_Screening_result,\n" +
                "    max(if(o.concept_id=164934,(case o.value_coded when 664 then \"Negative\" when 703 then \"Positive\" when 159008 then \"Suspicious for cancer\" when 5622 then \"Other\" else \"\" end),null)) as VIA_VILI_Screening_result,\n" +
                "    max(if(o.concept_id=164934,(case o.value_coded when 1115 then \"Normal\" when 1116 then \"Abnormal\" when 159008 then \"Suspicious for cancer\" when 5622 then \"Other\" else \"\" end),null)) as Colposcopy_Screening_result,\n" +
                "    max(if(o.concept_id=164934,(case o.value_coded when 1115 then \"Normal\" when 145808 then \"Low grade lesion\" when 145805 then \"High grade lesion\" when 155424 then \"Invasive Cancer\" when 5622 then \"Other\" else \"\" end),null)) as PAP_Smear_Screening_result,\n" +
                "\tmax(if(o.concept_id=164934,(case o.value_coded when 703 then \"Positive\" when 664 then \"Negative\" when 159393 then \"Presumed Cancer\" else \"\" end),null)) as HPV_Screening_result,\n" +
                "\tmax(if(o.concept_id=164934,(case o.value_coded when 664 then \"Negative\" when 703 then \"Positive\" when 159008 then \"Suspicious for cancer\" when 1115 then \"Normal\" when 145808 then \"Low grade lesion\" when 145805 then \"High grade lesion\" when 155424 then \"Invasive Cancer\" when 159393 then \"Presumed Cancer\" when 5622 then \"Other\" else \"\" end),null)) as Screening_result,\n" +
                "\tmax(if(o.concept_id=165266,(case o.value_coded when 165385 then \"Cryotherapy performed (single Visit)\" when 165381 then \"Cryotherapy postponed\" when 165386 then \"Cryotherapy performed (previously postponed)\" when 162810 then \"LEEP performed\" when 165396 then \"Cold knife cone\" when 165395 then \"Thermocoagulation\" when  159837 then \"Hysterectomy\" when 165391 then \"Referred for cancer treatment\" when 5622 then \"Other\" when 1107 then \"None\" else \"\" end),null)) as Treatment_today,\n" +
                "    #max(if(o.concept_id=163032,(case o.value_text else \"\" end),null)) as Explain,\n" +
                "\tmax(if(o.concept_id=165267,(case o.value_coded when 1065 then \"Yes\" when 1066 then \"No\" else \"\" end),null)) as Referred_out,\n" +
                "\t#max(if(o.concept_id=163032,(case o.value_text else \"\" end),null)) as Explain,\n" +
                "    max(if(o.concept_id=887,(case o.value_coded when 165388 then \"Site does not have cryotherapy machine\" when 132948 then \"Large lesion\" when 159008 then \"Suspect cancer\" when 5622 then \"Other gynaecological problem\" else \"\" end),null)) as Referral_reason,\n" +
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
                "\t\t\tuuid in('b69580d3-c242-4f2f-ae49-283f8e719b55 ')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (160288,164181,162816,163589,163589,164934,164934,164934,164934,164934,165266,165267,887)\n" +
                "where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0 \n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}