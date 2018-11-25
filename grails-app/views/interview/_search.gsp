<el:formGroup>
    <el:textField name="id" size="6" class=" "
                     label="${message(code: 'interview.id.label', default: 'id')}"/>
    <el:textField name="description" size="6" class=""
                 label="${message(code: 'interview.description.label', default: 'description')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="recruitmentCycle"
                     action="autocomplete" name="recruitmentCycle.id"
                     label="${message(code: 'interview.recruitmentCycle.label', default: 'recruitmentCycle')}"/>
    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'interview.fromDate.label')}"/>
</el:formGroup>

<el:formGroup>
    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'interview.toDate.label')}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class="" controller="vacancy" action="autocomplete"
                     name="vacancy.id" label="${message(code: 'interview.vacancy.label', default: 'vacancy')}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumInterviewStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus.values()}" name="interviewStatus" size="6"
               class="" label="${message(code: 'interview.interviewStatus.label', default: 'interviewStatus')}"/>

</el:formGroup>
<g:render template="/pcore/location/searchTemplate"
          model="[prefix: 'location']"/>

<g:render template="/pcore/location/script" />





