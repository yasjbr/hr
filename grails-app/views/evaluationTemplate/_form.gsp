
<g:render template="/DescriptionInfo/wrapper" model="[bean:evaluationTemplate?.descriptionInfo]" />
<el:formGroup>
    <el:select valueMessagePrefix="EnumEvaluationTemplateType"  from="${ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType.values()}" name="templateType" size="8"  class=" isRequired" label="${message(code:'evaluationTemplate.templateType.label',default:'templateType')}" value="${evaluationTemplate?.templateType}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=""
                     controller="militaryRank"
                     action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'evaluationTemplate.militaryRank.label', default: 'militaryRank')}"
                     values="${evaluationTemplate?.transientData?.militaryRanks?.collect {
                         [it?.id, it?.descriptionInfo?.localName]
                     }}"
                     multiple="true" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=""
                     controller="jobCategory"
                     action="autocomplete"
                     name="jobCategory.id"
                     label="${message(code: 'evaluationTemplate.jobCategory.label', default: 'jobCategory')}"
                     values="${evaluationTemplate?.transientData?.jobCategories?.collect {
                         [it?.id, it?.descriptionInfo?.localName]
                     }}"
                     multiple="true" />
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8"  class=" isRequired" label="${message(code:'evaluationTemplate.universalCode.label',default:'universalCode')}" value="${evaluationTemplate?.universalCode}"/>
</el:formGroup>