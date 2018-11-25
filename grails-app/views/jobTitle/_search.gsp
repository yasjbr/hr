
<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="8"
                  label="${messageValue?:(message(code:'descriptionInfo.localName.label',default:'localName'))}"/>
</el:formGroup><el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="jobCategory" action="autocomplete" name="jobCategory.id" label="${message(code:'jobTitle.jobCategory.label',default:'jobCategory')}" />
</el:formGroup>

