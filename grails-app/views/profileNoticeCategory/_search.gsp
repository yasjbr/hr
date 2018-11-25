<g:render template="/DescriptionInfo/wrapper" model="[bean:profileNoticeCategory?.descriptionInfo,isSearch:true]" />

<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'profileNoticeCategory.description.label',default:'description')}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'profileNoticeCategory.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'profileNoticeCategory.universalCode.label',default:'universalCode')}" />
</el:formGroup>
