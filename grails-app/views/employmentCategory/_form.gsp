
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:employmentCategory?.descriptionInfo]" />
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'employmentCategory.universalCode.label',default:'universalCode')}" value="${employmentCategory?.universalCode}"/>
</el:formGroup>