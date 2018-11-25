<g:render template="/DescriptionInfo/wrapper" model="[bean: vacationType?.descriptionInfo]"/>


<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="pcore"
                     action="colorAutoComplete"
                     name="colorId"
                     id="colorId"
                     label="${message(code: 'vacationType.colorId.label', default: 'color')}"
                     values="${[[vacationType?.colorId, vacationType?.transientData?.colorName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="excludedFromServicePeriod" size="8" isChecked="${vacationType?.excludedFromServicePeriod}" value="${vacationType?.excludedFromServicePeriod}" label="${message(code: 'vacationType.excludedFromServicePeriod.label')}" />
</el:formGroup>

<el:formGroup>
    <el:checkboxField name="needsExternalApproval" size="8" class=" "
                      label="${message(code: 'vacationType.needsExternalApproval.label', default: 'needsExternalApproval')}"
                      value="${vacationType?.needsExternalApproval}" isChecked="${vacationType?.needsExternalApproval}"/>

</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'vacationType.universalCode.label', default: 'universalCode')}"
                  value="${vacationType?.universalCode}"/>
</el:formGroup>