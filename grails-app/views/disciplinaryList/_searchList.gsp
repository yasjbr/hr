<el:formGroup>
    <el:textField name="disciplinaryRequest.id" size="6"
                     class=""
                     label="${message(code: 'disciplinaryRequest.id.label', default: 'id')}"/>
    <g:render template="/employee/wrapper" model="[isSearch     : true,
                                                   withOutForm  : true,
                                                   size         : 6]"/>
</el:formGroup>

<el:formGroup>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="department"
            action="autocomplete"
            name="department.id"
            label="${message(code: 'employee.currentEmploymentRecord.department.descriptionInfo.localName.label', default: 'department')}"/>

    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>

</el:formGroup>



<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="disciplinaryCategory" action="autocomplete" name="disciplinaryCategory.id"
                     label="${message(code: 'disciplinaryRequest.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>

    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'disciplinaryRequest.requestDate.label')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=""
                     controller="disciplinaryJudgment" action="autocomplete"

                     name="disciplinaryJudgment.id" label="${message(code:'disciplinaryRecordJudgment.disciplinaryJudgment.label',default:'disciplinaryJudgment')}" />

    <el:select valueMessagePrefix="EnumJudgmentStatus" from="${ps.gov.epsilon.hr.enums.disciplinary.v1.EnumJudgmentStatus.values()}"
               name="judgmentStatus" size="6"  class="" label="${message(code:'disciplinaryRecordJudgment.judgmentStatus.label',default:'judgmentStatus')}" />


</el:formGroup>

