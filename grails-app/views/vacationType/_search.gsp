
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="${size?:"8"}"
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"
                  value="${bean?.localName}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="pcore"
                     action="colorAutoComplete"
                     name="colorId"
                     id="colorId"
                     label="${message(code:'vacationType.colorId.label',default:'color')}"/>
</el:formGroup>

<el:formGroup>
    <el:select label="${message(code:'vacationType.excludedFromServicePeriod.label',default:'excludedFromServicePeriod')}"
               name="excludedFromServicePeriod"
               size="8"
               class="" from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>
