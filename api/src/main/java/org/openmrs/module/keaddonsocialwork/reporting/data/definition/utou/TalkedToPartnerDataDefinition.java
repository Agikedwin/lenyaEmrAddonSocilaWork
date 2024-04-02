package org.openmrs.module.keaddonsocialwork.reporting.data.definition.utou;

import org.openmrs.module.reporting.data.BaseDataDefinition;
import org.openmrs.module.reporting.data.encounter.definition.EncounterDataDefinition;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

@Caching(strategy = ConfigurationPropertyCachingStrategy.class)

public class TalkedToPartnerDataDefinition extends BaseDataDefinition implements EncounterDataDefinition {

    public static long serialVersionUID = 1L;

    public TalkedToPartnerDataDefinition() {
        super();
    }

    public TalkedToPartnerDataDefinition(String name) {
        super(name);
    }
    /**
     * @see org.openmrs.module.reporting.data.DataDefinition#getDataType()
     */
    public Class<?> getDataType() {
        return String.class;
    }

}
