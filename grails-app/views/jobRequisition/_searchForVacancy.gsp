<el:hiddenField type="enum" name="requisitionStatus"  value="APPROVED" />
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycleId"
                     label="${message(code: 'jobRequisition.recruitmentCycle.label', default: 'recruitmentCycle')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="job" action="autocomplete"
                     name="jobId" label="${message(code: 'jobRequisition.job.label', default: 'job')}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="requestDate" size="8" class=""
                  label="${message(code: 'jobRequisition.requestDate.label', default: 'requestDate')}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfPositionsForSearch" size="8" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfPositions.label', default: 'numberOfPositions')}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfApprovedPositions" size="8" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfApprovedPositions.label', default: 'numberOfApprovedPositions')}"/>
</el:formGroup>


