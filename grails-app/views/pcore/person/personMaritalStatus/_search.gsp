<g:render template="/pcore/person/wrapper" model="[bean:personMaritalStatus?.person,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="maritalStatus" action="autocomplete" name="maritalStatus.id" label="${message(code:'personMaritalStatus.maritalStatus.label',default:'personMaritalStatus')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"  size="8" class="" label="${message(code:'personMaritalStatus.fromDate.label',default:'fromDate')}"  />
</el:formGroup>
<el:formGroup>
    <el:dateField name="toDate"  size="8" class="" label="${message(code:'personMaritalStatus.toDate.label',default:'toDate')}"  />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personMaritalStatus.note.label',default:'note')}" />
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isCurrent" size="8"  class="" label="${message(code:'personMaritalStatus.isCurrent.label',default:'isCurrent')}"  />
</el:formGroup>