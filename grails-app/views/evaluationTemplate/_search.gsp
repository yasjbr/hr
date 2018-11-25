<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="descriptionInfo" action="autocomplete" name="descriptionInfo.id" label="${message(code:'evaluationTemplate.descriptionInfo.label',default:'descriptionInfo')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'evaluationTemplate.firm.label',default:'firm')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumEvaluationTemplateType" from="${ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType.values()}" name="templateType" size="8"  class="" label="${message(code:'evaluationTemplate.templateType.label',default:'templateType')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'evaluationTemplate.universalCode.label',default:'universalCode')}" />
</el:formGroup>
