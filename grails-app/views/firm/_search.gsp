<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'firm.id.label', default: 'id')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="name" size="8" class="" label="${message(code: 'firm.name.label', default: 'name')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="code" size="8" class="" label="${message(code: 'firm.code.label', default: 'code')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=""
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="coreOrganizationId"
                     label="${message(code: 'firm.coreName.label', default: 'coreOrganizationId')}"/>

</el:formGroup>

