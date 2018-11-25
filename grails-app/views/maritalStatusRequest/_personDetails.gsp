
<el:formGroup>
    <el:textField type="text" isDisabled="true" name="personName" size="4" class=""
                  label="${message(code: 'employee.personName.label', default: 'personName')}"
                  value="${personDTO?.localFullName}"/>
    <el:textField type="text" isDisabled="true" name="recentCardNo" size="4" class=""
                  label="${message(code: 'person.recentCardNo.label', default: 'recentCardNo')}"
                  value="${personDTO?.recentCardNo}"/>
    <el:textField type="text" isDisabled="true" name="genderType" size="4" class=""
                  label="${message(code: 'person.genderType.label', default: 'genderType')}"
                  value="${personDTO?.genderType?.descriptionInfo?.localName}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField type="text" isDisabled="true" name="dateOfBirth" size="4" class=""
                  label="${message(code: 'person.dateOfBirth.label', default: 'dateOfBirth')}"
                  value="${personDTO?.dateOfBirth}"/>

    <el:textField type="text" isDisabled="true" name="birthPlace" size="4" class=""
                  label="${message(code: 'person.birthPlace.label', default: 'birthPlace')}"
                  value="${personDTO?.birthPlace?.toString()}"/>

    <el:textField type="text" isDisabled="true" name="localMotherName" size="4" class=""
                  label="${message(code: 'person.localMotherName.label', default: 'localMotherName')}"
                  value="${personDTO?.localMotherName}"/>
</el:formGroup>