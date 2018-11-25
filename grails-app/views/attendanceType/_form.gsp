<g:render template="/DescriptionInfo/wrapper" model="[bean:attendanceType?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'attendanceType.universalCode.label',default:'universalCode')}" value="${attendanceType?.universalCode}"/>
</el:formGroup>