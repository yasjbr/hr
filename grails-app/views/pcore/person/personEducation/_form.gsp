
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                             name:'person.id',
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                             bean:personEducation?.person,
                                             isDisabled:isPersonDisabled]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="educationDegree" action="autocomplete" name="educationDegree.id" label="${message(code:'personEducation.educationDegree.label',default:'educationDegree')}" values="${[[personEducation?.educationDegree?.id,personEducation?.educationDegree?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationMajor" action="autocomplete" name="educationMajor.id" label="${message(code:'personEducation.educationMajor.label',default:'educationMajor')}" values="${[[personEducation?.educationMajor?.id,personEducation?.educationMajor?.descriptionInfo?.localName]]}" />
</el:formGroup>



<el:formGroup>
    <el:dualAutocomplete class=" isRequired" label="${message(code:'personEducation.organization.label',default:'organization')}"
                         id="organizationId" name="organization.id" action="autocomplete"
                         controller="organization" size="8"
                         values="${[[personEducation?.organization?.id,personEducation?.organization?.descriptionInfo?.localName]]}"
                         textName="instituteName"
                         textValue="${personEducation?.instituteName}"
    />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="educationLevel" action="autocomplete" name="educationLevel.id" label="${message(code:'personEducation.educationLevel.label',default:'educationLevel')}" values="${[[personEducation?.educationLevel?.id,personEducation?.educationLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>

    <el:dateField zoned="true" name="obtainingDate" size="8" class=" "
                  label="${message(code: 'personEducation.obtainingDate.label', default: 'obtainingDate')}"
                  value="${personEducation?.obtainingDate}"/>

</el:formGroup>

<el:formGroup>
    <el:textField name="periodInYear" size="8" class=" "
                  label="${message(code: 'personEducation.periodInYear.label', default: 'periodInYear')}"
                  value="${personEducation?.periodInYear}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="totalHours" size="8" class=" "
                  label="${message(code: 'personEducation.totalHours.label', default: 'totalHours')}"
                  value="${personEducation?.totalHours}"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'personEducation.location.label')}">
    <g:render template="/pcore/location/wrapper"
              model="[
                      isCountryRequired         : true,
                      hiddenDetails             : true,
                      size                      : 8,
                      location:personEducation?.location]" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${personEducation?.unstructuredLocation}"/>
    </el:formGroup>
</lay:wall>