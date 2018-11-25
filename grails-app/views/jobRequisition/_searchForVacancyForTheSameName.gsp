<el:hiddenField type="enum" name="requisitionStatus" value="APPROVED"/>
<el:hiddenField name="name" value="${vacancy?.job}"/>


<el:formGroup>
    <el:dateField name="fromRequestDate.theSameName" size="6" class="" setMinDateFor="toRequestDate.theSameName"
                  label="${message(code: 'allowanceRequest.fromRequestDate.label', default: 'toDate')}"/>
    <el:dateField name="toRequestDate.theSameName" size="6" class=""
                  label="${message(code: 'allowanceRequest.toRequestDate.label', default: 'toDate')}"/>
</el:formGroup>


<el:formGroup>
    <el:integerField name="numberOfPositionsForSearch.theSameName" size="6" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfPositions.label', default: 'numberOfPositions')}"/>
    <el:integerField name="numberOfApprovedPositions.theSameName" size="6" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfApprovedPositions.label', default: 'numberOfApprovedPositions')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycleId.theSameName"
                     label="${message(code: 'jobRequisition.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

</el:formGroup>

