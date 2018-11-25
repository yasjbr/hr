
<el:formGroup>
    <el:select valueMessagePrefix="EnumAbsenceReason"  from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}" name="actualAbsenceReason" size="8"  class=" isRequired" label="${message(code:'returnFromAbsenceListEmployee.actualAbsenceReason.label',default:'actualAbsenceReason')}" value="${returnFromAbsenceListEmployee?.actualAbsenceReason}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="actualReturnDate"  size="8" class=" isRequired" label="${message(code:'returnFromAbsenceListEmployee.actualReturnDate.label',default:'actualReturnDate')}" value="${returnFromAbsenceListEmployee?.actualReturnDate}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class=" isRequired" label="${message(code:'returnFromAbsenceListEmployee.recordStatus.label',default:'recordStatus')}" value="${returnFromAbsenceListEmployee?.recordStatus}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="returnFromAbsenceList" action="autocomplete" name="returnFromAbsenceList.id" label="${message(code:'returnFromAbsenceListEmployee.returnFromAbsenceList.label',default:'returnFromAbsenceList')}" values="${[[returnFromAbsenceListEmployee?.returnFromAbsenceList?.id,returnFromAbsenceListEmployee?.returnFromAbsenceList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="returnFromAbsenceRequest" action="autocomplete" name="returnFromAbsenceRequest.id" label="${message(code:'returnFromAbsenceListEmployee.returnFromAbsenceRequest.label',default:'returnFromAbsenceRequest')}" values="${[[returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.id,returnFromAbsenceListEmployee?.returnFromAbsenceRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>