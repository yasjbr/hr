
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="disciplinaryJudgment" action="autocomplete" name="disciplinaryJudgment.id" label="${message(code:'disciplinaryRecordJudgment.disciplinaryJudgment.label',default:'disciplinaryJudgment')}" values="${[[disciplinaryRecordJudgment?.disciplinaryJudgment?.id,disciplinaryRecordJudgment?.disciplinaryJudgment?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="disciplinaryList" action="autocomplete" name="disciplinaryRecordsList.id" label="${message(code:'disciplinaryRecordJudgment.disciplinaryRecordsList.label',default:'disciplinaryRecordsList')}" values="${[[disciplinaryRecordJudgment?.disciplinaryRecordsList?.id,disciplinaryRecordJudgment?.disciplinaryRecordsList?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="disciplinaryRequest" action="autocomplete" name="disciplinaryRequest.id" label="${message(code:'disciplinaryRecordJudgment.disciplinaryRequest.label',default:'disciplinaryRequest')}" values="${[[disciplinaryRecordJudgment?.disciplinaryRequest?.id,disciplinaryRecordJudgment?.disciplinaryRequest?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm" action="autocomplete" name="firm.id" label="${message(code:'disciplinaryRecordJudgment.firm.label',default:'firm')}" values="${[[disciplinaryRecordJudgment?.firm?.id,disciplinaryRecordJudgment?.firm?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumJudgmentStatus"  from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus.values()}" name="judgmentStatus" size="8"  class=" isRequired" label="${message(code:'disciplinaryRecordJudgment.judgmentStatus.label',default:'judgmentStatus')}" value="${disciplinaryRecordJudgment?.judgmentStatus}" />
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8"  class="" label="${message(code:'disciplinaryRecordJudgment.note.label',default:'note')}" value="${disciplinaryRecordJudgment?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'disciplinaryRecordJudgment.orderNo.label',default:'orderNo')}" value="${disciplinaryRecordJudgment?.orderNo}"/>
</el:formGroup>
<el:formGroup>
    <el:integerField name="unitId" size="8"  class=" isNumber" label="${message(code:'disciplinaryRecordJudgment.unitId.label',default:'unitId')}" value="${disciplinaryRecordJudgment?.unitId}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="value" size="8"  class="" label="${message(code:'disciplinaryRecordJudgment.value.label',default:'value')}" value="${disciplinaryRecordJudgment?.value}"/>
</el:formGroup>