<g:render template="/pcore/person/wrapper" model="[bean:personEmploymentHistory?.person,isSearch:true]" />

<el:formGroup>
    <el:textField name="jobDescription" size="8"  class="" label="${message(code:'personEmploymentHistory.jobDescription.label',default:'jobDescription')}" value="${personEmploymentHistory?.jobDescription}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="organization" action="autocomplete" name="organization.id" label="${message(code:'personEmploymentHistory.organization.label',default:'organization')}" values="${[[personEmploymentHistory?.organization?.id,personEmploymentHistory?.organization?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="organizationName" size="8"  class=" " label="${message(code:'personEmploymentHistory.organizationName.label',default:'organizationName')}" value="${personEmploymentHistory?.organizationName}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="professionType" action="autocomplete" name="professionType.id" label="${message(code:'personEmploymentHistory.professionType.label',default:'professionType')}" values="${[[personEmploymentHistory?.professionType?.id,personEmploymentHistory?.professionType?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class=" " label="${message(code:'personEmploymentHistory.fromDate.label',default:'fromDate')}" value="${personEmploymentHistory?.fromDate}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class=" " label="${message(code:'personEmploymentHistory.toDate.label',default:'toDate')}" value="${personEmploymentHistory?.toDate}" />
</el:formGroup>

<lay:wall title="${g.message(code: "location.label")}">
    <g:render template="/pcore/location/searchWrapper" />
    <el:formGroup>
        <el:textArea name="unstructuredLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" />
    </el:formGroup>
</lay:wall>