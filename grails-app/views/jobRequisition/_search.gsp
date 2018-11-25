<el:formGroup>

    <el:textField name="id" size="6" class=" "
                  label="${message(code: 'vacancy.id.label', default: 'id')}"/>


    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycle.id"
                     label="${message(code: 'jobRequisition.recruitmentCycle.label', default: 'recruitmentCycle')}"/>

</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="department" action="autocomplete"
                     name="requestedForDepartment.id"
                     label="${message(code: 'jobRequisition.requestedForDepartment.label', default: 'requestedForDepartment')}"/>



    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="job" action="autocomplete"
                     name="job.id" label="${message(code: 'jobRequisition.job.label', default: 'job')}"/>

</el:formGroup>

<el:formGroup>

    <el:range type="date" name="requestDate" size="6" class=""
              label="${message(code: 'jobRequisition.requestDate.label', default: 'requestDate')}"/>


    <el:integerField name="numberOfPositions" size="6" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfPositions.label', default: 'numberOfPositions')}"/>
</el:formGroup>

<el:formGroup>
    <el:integerField name="numberOfApprovedPositions" size="6" class=" isNumber"
                     label="${message(code: 'jobRequisition.numberOfApprovedPositions.label', default: 'numberOfApprovedPositions')}"/>

    <el:select valueMessagePrefix="EnumRequestStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.values()}"
               name="requisitionStatus" size="6" class=""
               label="${message(code: 'jobRequisition.requisitionStatus.label', default: 'requisitionStatus')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="rejectionReason"
                  size="6"
                  class=""
                  label="${message(code: 'jobRequisition.note.label', default: 'note')}"/>
</el:formGroup>



