<el:row>
    <el:validatableResetForm callBackFunction="selectPerson" name="applicantForm1" controller="applicant" action="getPerson">

        <el:formGroup>
            <el:autocomplete
                    optionKey="id"
                    optionValue="name"
                    size="8"
                    class=" isRequired"
                    controller="person"
                    action="autocomplete"
                    name="personId"
                    label="${message(code: 'applicant.searchPerson.label', default: 'searchPerson')}"/>
        </el:formGroup>

        <div class="clearfix" style="text-align: center;padding: 15px;">
            <btn:selectButton isSubmit="true"/>
            <btn:addButton onclick="window.location.href='${createLink(controller: 'applicant', action: 'createNewPerson')}'"/>
        </div>
    </el:validatableResetForm>
</el:row>