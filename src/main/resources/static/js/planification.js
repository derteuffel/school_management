const callInputPlanning = document.getElementsByClassName('callInputPlanning')
const crewCallPlanning = document.getElementsByClassName('crewCallPlanning')
const removeCallPlanning = document.getElementsByClassName('removeCallPlanning')
/* crew call */
let userss= [$("#directorEmail").val()]
let removeToTheCallListPlanning
removeToTheCallListPlanning = (e)=>{
    if(userss.indexOf(e.currentTarget.param)!==-1) {
        userss.splice(userss.indexOf(e.currentTarget.param), 1)
        console.log(userss)
        crewCallPlanning[e.currentTarget.indice].style.display="inline"
        removeCallPlanning[e.currentTarget.indice].style.display="none"
    }
}
let addToTheCallListPlanning
addToTheCallListPlanning = (e)=>{
    if(userss.indexOf(e.currentTarget.param)==-1)
    {
        userss.push(e.currentTarget.param)
        crewCallPlanning[e.currentTarget.indice].style.display="none"
        removeCallPlanning[e.currentTarget.indice].style.display="inline"
        console.log(userss)
    }

}

/* crew call */
for(i=0;i<=crewCallPlanning.length-1;i++){
    crewCallPlanning[i].param = callInputPlanning[i].value
    crewCallPlanning[i].indice = i
    crewCallPlanning[i].addEventListener('click',addToTheCallListPlanning)
    removeCallPlanning[i].param = callInputPlanning[i].value
    removeCallPlanning[i].indice = i
    removeCallPlanning[i].addEventListener('click',removeToTheCallListPlanning)
    removeCallPlanning[i].style.display="none"

}
$("#planifier").click(()=>{
    const date = $("#datePlanning").val()
    userss.map(async v=> await fetch(`https://ecoles.yesbanana.org/planning/${v}?date=${date}`))
    $("#planningSuccess").css('display','flex')
    setTimeout(()=>$("#planningSuccess").css('display','none'),3000)
})



