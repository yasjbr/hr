
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeePromotion" action="autocomplete" name="currentEmployeeMilitaryRank.id" label="${message(code:'request.currentEmployeeMilitaryRank.label',default:'currentEmployeeMilitaryRank')}" values="${[[request?.currentEmployeeMilitaryRank?.id,request?.currentEmployeeMilitaryRank?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employmentRecord" action="autocomplete" name="currentEmploymentRecord.id" label="${message(code:'request.currentEmploymentRecord.label',default:'currentEmploymentRecord')}" values="${[[request?.currentEmploymentRecord?.id,request?.currentEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employmentRecord" action="autocomplete" name="currentRequesterEmploymentRecord.id" label="${message(code:'request.currentRequesterEmploymentRecord.label',default:'currentRequesterEmploymentRecord')}" values="${[[request?.currentRequesterEmploymentRecord?.id,request?.currentRequesterEmploymentRecord?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee" action="autocomplete" name="employee.id" label="${message(code:'request.employee.label',default:'employee')}" values="${[[request?.employee?.id,request?.employee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'request.firm.label',default:'firm')}" values="${[[request?.firm?.id,request?.firm?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="parentRequestId" size="8"  class=" isNumber" label="${message(code:'request.parentRequestId.label',default:'parentRequestId')}" value="${request?.parentRequestId}" />
</el:formGroup>
<el:formGroup>
    <el:dateField name="requestDate"  size="8" class=" isRequired" label="${message(code:'request.requestDate.label',default:'requestDate')}" value="${request?.requestDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="requestReason" size="8"  class="" label="${message(code:'request.requestReason.label',default:'requestReason')}" value="${request?.requestReason}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestStatus"  from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}" name="requestStatus" size="8"  class=" isRequired" label="${message(code:'request.requestStatus.label',default:'requestStatus')}" value="${request?.requestStatus}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="requestStatusNote" size="8"  class="" label="${message(code:'request.requestStatusNote.label',default:'requestStatusNote')}" value="${request?.requestStatusNote}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType"  from="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.values()}" name="requestType" size="8"  class=" isRequired" label="${message(code:'request.requestType.label',default:'requestType')}" value="${request?.requestType}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="employee" action="autocomplete" name="requester.id" label="${message(code:'request.requester.label',default:'requester')}" values="${[[request?.requester?.id,request?.requester?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="department" action="autocomplete" name="requesterDepartment.id" label="${message(code:'request.requesterDepartment.label',default:'requesterDepartment')}" values="${[[request?.requesterDepartment?.id,request?.requesterDepartment?.descriptionInfo?.localName]]}" />
</el:formGroup>