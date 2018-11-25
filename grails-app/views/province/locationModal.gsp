<el:validatableModalForm title="${message(code: 'location.label')}" name="locationModal" id="locationModal">

    <msg:modal/>
    <el:formGroup>
        <el:autocomplete optionKey="id"
                         optionValue="name"
                         size="8"
                         class=" isRequired"
                         id="countryId"
                         controller="pcore"
                         action="countryAutoComplete"
                         name="country.id"
                         label="${message(code: 'location.country.label', default: 'country')}"/>
    </el:formGroup>

    <el:formGroup>
        <el:autocomplete optionKey="id" optionValue="name" size="8"
                         paramsGenerateFunction="governorateParams"
                         id="governorateId" class=" "
                         controller="pcore" action="governorateAutoComplete"
                         name="governorate.id"
                         label="${message(code: 'location.governorate.label', default: 'governorate')}"/>
    </el:formGroup>


    <el:formButton onClick="insertLocationIntoLocationTable()" functionName="save"/>
</el:validatableModalForm>
