
<g:render template="/DescriptionInfo/wrapper" model="[bean:departmentType?.descriptionInfo]" />
<el:formGroup>
    <el:select valueMessagePrefix="EnumDepartmentType"  from="${ps.gov.epsilon.hr.enums.v1.EnumDepartmentType.values()}" name="staticDepartmentType" size="8"  class=" isRequired" label="${message(code:'departmentType.staticDepartmentType.label',default:'staticDepartmentType')}" value="${departmentType?.staticDepartmentType}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'departmentType.universalCode.label',default:'universalCode')}" value="${departmentType?.universalCode}"/>
</el:formGroup>