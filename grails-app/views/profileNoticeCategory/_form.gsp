<g:render template="/DescriptionInfo/wrapper" model="[bean:profileNoticeCategory?.descriptionInfo]" />

<el:formGroup>
    <el:textArea name="description" size="8"  class="" label="${message(code:'profileNoticeCategory.description.label',default:'description')}" value="${profileNoticeCategory?.description}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'profileNoticeCategory.firm.label',default:'firm')}" values="${[[profileNoticeCategory?.firm?.id,profileNoticeCategory?.firm?.name]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'profileNoticeCategory.universalCode.label',default:'universalCode')}" value="${profileNoticeCategory?.universalCode}"/>
</el:formGroup>