
<g:render template="/DescriptionInfo/wrapper" model="[bean:attendanceType?.descriptionInfo,isSearch:true]" />

<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'attendanceType.universalCode.label',default:'universalCode')}" />
</el:formGroup>