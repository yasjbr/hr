<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personEmploymentHistory?.person,
                                             isDisabled:isPersonDisabled]" />

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="professionType" action="autocomplete" name="professionType.id" label="${message(code:'personEmploymentHistory.professionType.label',default:'professionType')}" values="${[[personEmploymentHistory?.professionType?.id,personEmploymentHistory?.professionType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:dualAutocomplete class=" isRequired" label="${message(code:'personEmploymentHistory.organization.label',default:'organization')}"
                         id="organizationId" name="organization.id" action="autocomplete"
                         controller="organization" size="8"
                         values="${[[personEmploymentHistory?.organization?.id,personEmploymentHistory?.organization?.descriptionInfo?.localName]]}"
                         textName="organizationName"
                         textValue="${personEmploymentHistory?.organizationName}"
    />
</el:formGroup>


<el:formGroup>
    <el:textField name="jobDescription" size="8"  class="" label="${message(code:'personEmploymentHistory.jobDescription.label',default:'jobDescription')}" value="${personEmploymentHistory?.jobDescription}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromDate"  size="8" setMinDateFor="toDate" isMaxDate="true" class=" isRequired" label="${message(code:'personEmploymentHistory.fromDate.label',default:'fromDate')}" value="${personEmploymentHistory?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate" size="8" class=" " label="${message(code:'personEmploymentHistory.toDate.label',default:'toDate')}" value="${personEmploymentHistory?.toDate}" />
</el:formGroup>


<lay:wall title="${g.message(code: 'personEmploymentHistory.location.label')}">
    <g:render template="/pcore/location/wrapper"
              model="[
                      isCountryRequired         : true,
                      hiddenDetails             : true,
                      size                      : 8,
                      location:personEmploymentHistory?.location]" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${personEmploymentHistory?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>