<g:if test="${vacationConfiguration?.id != null}">
    <el:formGroup>
        <el:labelField label="${message(code: 'vacationConfiguration.vacationType.label', default: 'vacationType')}"
                       value="${vacationConfiguration?.vacationType?.descriptionInfo?.localName}" size="8"/>
        <el:hiddenField name="vacationType.id" value="${vacationConfiguration?.vacationType?.id}"/>
    </el:formGroup>

    <el:formGroup>
        <el:labelField label="${message(code: 'vacationConfiguration.militaryRank.label', default: 'militaryRank')}"
                       value="${vacationConfiguration?.militaryRank?.descriptionInfo?.localName}" size="8"/>
        <el:hiddenField name="militaryRank.id" value="${vacationConfiguration?.militaryRank?.id}"/>
    </el:formGroup>
</g:if>

<g:else>
    <el:formGroup>
        <el:select name="vacationType.id" from="${vacationTypeList}" optionKey="id" optionValue="descriptionInfo"
                   size="8" class=" isRequired"
                   label="${message(code: 'vacationConfiguration.vacationType.label', default: 'vacationType')}"/>
    </el:formGroup>
    <el:formGroup>
        <el:select name="militaryRankIds" from="${militaryRankList}" optionKey="id" optionValue="descriptionInfo"
                   size="8" class=" isRequired" multiple="true"
                   label="${message(code: 'vacationConfiguration.militaryRank.label', default: 'militaryRank')}"
                   values="${[[vacationConfiguration?.militaryRank?.id, vacationConfiguration?.militaryRank?.descriptionInfo?.localName]]}"/>
    </el:formGroup>
</g:else>




<el:formGroup>
    <el:integerField name="maxAllowedValue" size="8" class=" isRequired isNumber"
                     label="${message(code: 'vacationConfiguration.maxAllowedValue.label', default: 'maxAllowedValue')}"
                     value="${vacationConfiguration?.maxAllowedValue}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="maxBalance" size="8" class="  isNumber"
                     label="${message(code: 'vacationConfiguration.maxBalance.label', default: 'maxBalance')}"
                     value="${vacationConfiguration?.maxBalance}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="allowedValue" size="8" class="  isNumber"
                     label="${message(code: 'vacationConfiguration.allowedValue.label', default: 'allowedValue')}"
                     value="${vacationConfiguration?.allowedValue}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="employmentPeriod" size="8" class="  isNumber"
                     label="${message(code: 'vacationConfiguration.employmentPeriod.label', default: 'employmentPeriod')}"
                     value="${vacationConfiguration?.employmentPeriod}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="frequency" size="8" class="  isNumber"
                     label="${message(code: 'vacationConfiguration.frequency.label', default: 'frequency')}"
                     value="${vacationConfiguration?.frequency}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumSexAccepted"
               from="${ps.gov.epsilon.hr.enums.jobRequisition.v1.EnumSexAccepted.values()}" name="sexTypeAccepted"
               size="8" class=" isRequired"
               label="${message(code: 'vacationConfiguration.sexTypeAccepted.label', default: 'sexTypeAccepted')}"
               value="${vacationConfiguration?.sexTypeAccepted}"/>
</el:formGroup>
%{--<el:formGroup>--}%
%{--<el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="pcore"--}%
%{--action="maritalStatusAutoComplete" name="maritalStatusId"--}%
%{--label="${message(code: 'vacationConfiguration.maritalStatusId.label', default: 'maritalStatusId')}"--}%
%{--values="${[[vacationConfiguration?.maritalStatusId, vacationConfiguration?.transientData?.maritalStatusName]]}"/>--}%
%{--</el:formGroup>--}%

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="pcore"
                     action="religionAutoComplete"
                     name="religionId"
                     label="${message(code: 'vacationConfiguration.religionId.label', default: 'religionId')}"
                     values="${[[vacationConfiguration?.religionId, vacationConfiguration?.transientData?.religionName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="checkForAnnualLeave" size="8" class=" "
                      label="${message(code: 'vacationConfiguration.checkForAnnualLeave.label', default: 'checkForAnnualLeave')}"
                      value="${vacationConfiguration?.checkForAnnualLeave}"
                      isChecked="${vacationConfiguration?.checkForAnnualLeave}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="isExternal" size="8" class=" "
                      label="${message(code: 'vacationConfiguration.isExternal.label', default: 'isExternal')}"
                      value="${vacationConfiguration?.isExternal}" isChecked="${vacationConfiguration?.isExternal}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isBreakable" size="8" class=" "
                      label="${message(code: 'vacationConfiguration.isBreakable.label', default: 'isBreakable')}"
                      value="${vacationConfiguration?.isBreakable}" isChecked="${vacationConfiguration?.isBreakable}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="takenFully" size="8" class=" "
                      label="${message(code: 'vacationConfiguration.takenFully.label', default: 'takenFully')}"
                      value="${vacationConfiguration?.takenFully}" isChecked="${vacationConfiguration?.takenFully}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="isTransferableToNewYear" id="isTransferableToNewYear"
                      onchange="vacationTransferValueSettings(this);" size="8"
                      class=" "
                      label="${message(code: 'vacationConfiguration.isTransferableToNewYear.label', default: 'isTransferableToNewYear')}"
                      value="${params.action == 'create' ? true : vacationConfiguration?.isTransferableToNewYear}"
                      isChecked="${params.action == 'create' ? true : vacationConfiguration?.isTransferableToNewYear}"/>
</el:formGroup>

<el:formGroup id="vacationTransferValue">
    <el:decimalField name="vacationTransferValue" size="8" class=" isRequired isDecimal"
                     label="${message(code: 'vacationConfiguration.vacationTransferValue.label', default: 'vacationTransferValue')}"
                     value="${vacationConfiguration?.vacationTransferValue}"/>
</el:formGroup>




