<g:render template="/pcore/person/wrapper" model="[bean:personNationality?.person,isSearch:true]" />
<el:formGroup>
    <el:dateField name="acquiredDate"  size="8" class=" "
                  label="${message(code:'personNationality.acquiredDate.label',default:'acquiredDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" "
                     controller="nationalityAcquisitionMethod" action="autocomplete" name="acquisitionMethod.id"
                     label="${message(code:'personNationality.acquisitionMethod.label',default:'acquisitionMethod')}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="expiryDate"  size="8" class=" "
                  label="${message(code:'personNationality.expiryDate.label',default:'expiryDate')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="country"
                     action="autocomplete" name="granterCountry.id"
                     label="${message(code:'personNationality.granterCountry.label',default:'granterCountry')}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="legalIdentifier"
                     action="autocomplete" name="legalIdentifier.id"
                     label="${message(code:'personNationality.legalIdentifier.label',default:'legalIdentifier')}" />
</el:formGroup>