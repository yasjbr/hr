<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="vacationType" action="autocomplete"
                     name="vacationType.id"
                     label="${message(code: 'vacationConfiguration.vacationType.label', default: 'vacationType')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="militaryRank" action="autocomplete"
                     name="militaryRank.id"
                     label="${message(code: 'vacationConfiguration.militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="maxAllowedValue" size="8" class=" isNumber"
                     label="${message(code: 'vacationConfiguration.maxAllowedValue.label', default: 'maxAllowedValue')}"/>
</el:formGroup>

<el:formGroup>
    <el:select label="${message(code: 'vacationConfiguration.isTransferableToNewYear.label', default: 'isTransferableToNewYear')}"
               name="isTransferableToNewYear"
               size="8"
               class="" from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>
</el:formGroup>

