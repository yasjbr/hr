<el:formGroup>

    <el:textField name="loanRequest.id" size="6"
                     class=""
                     label="${message(code:'loanRequestRelatedPerson.loanRequest.id.label',default:'id')}" />

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="person"
            action="autocomplete"
            name="requestedPersonId"
            label="${message(code: 'loanRequestRelatedPerson.transientData.requestedPersonDTO.label', default: 'requestedPerson')}"/>

</el:formGroup>
