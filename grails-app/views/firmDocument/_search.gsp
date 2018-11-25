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