
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationSection?.descriptionInfo,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="evaluationTemplate" action="autocomplete" name="evaluationTemplate.id" label="${message(code:'evaluationSection.evaluationTemplate.label',default:'evaluationTemplate')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="index" size="8"  class=" isNumber" label="${message(code:'evaluationSection.index.label',default:'index')}" />
</el:formGroup>
