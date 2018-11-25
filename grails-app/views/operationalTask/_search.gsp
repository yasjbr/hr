<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'operationalTask.id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField class="isRequired"
                  name="descriptionInfo.localName" size="8"
                  label="${message(code:'descriptionInfo.localName.label',default:'localName')}"
                  value="" />
</el:formGroup>
