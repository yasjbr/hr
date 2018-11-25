<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationItem?.descriptionInfo,isSearch:true]" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="evaluationSection" action="autocomplete" name="evaluationSection.id" label="${message(code:'evaluationItem.evaluationSection.label',default:'evaluationSection')}" />
</el:formGroup>

<el:formGroup>
    <el:integerField name="index" size="8"  class=" isNumber" label="${message(code:'evaluationItem.index.label',default:'index')}" />
</el:formGroup>

<el:formGroup>
    <el:decimalField name="maxMark" size="8"  class=" isDecimal" label="${message(code:'evaluationItem.maxMark.label',default:'maxMark')}"  />
</el:formGroup>