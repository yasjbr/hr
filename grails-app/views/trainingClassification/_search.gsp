
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="code" size="8"  class="" label="${message(code:'trainingClassification.code.label',default:'code')}" />
</el:formGroup>


<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="8"
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"/>
</el:formGroup>

