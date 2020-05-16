VoxeetSDK.initialize('N2wzZXJrdG1zcTc3cQ==', 'NzRqZ2pocGNmdmNxa2Q5YjZob2FoYWQ0MzU=')
VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
    addVideoNode(participant, stream);
})
VoxeetSDK.conference.on('streamRemoved', (participant) => {
    removeVideoNode(participant);
})
const removeVideoNode = (participant) => {
    let videoNode = document.getElementById('video-' + participant.id);

    if (videoNode) {
        videoNode.parentNode.removeChild(videoNode);
    }
}

const main = async (name,to) => {
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                    fetch(`https://ecoles.yesbanana.org/sendMail/${to}/${conference.id}`).then(()=>{
                        console.log('great')
                    })
                    return VoxeetSDK.conference.join(conference, {})
            }
            )
            .then(() => {

                VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).then(
                    ()=>{
                        $('#body').preloader('remove')
                        document.getElementById('video-super-container').style.display='flex'
                          }
                ).catch(e=>{console.log(e);$('#body').preloader('remove')})



            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }
}
const interval = ()=>{
    const date = new Date()
    setInterval(()=>{
        const distance= new Date().getTime()- date.getTime()
        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000)

        minutes = minutes < 10 ? "0" + minutes : minutes
        seconds = seconds < 10 ? "0" + seconds : seconds
        $('#countDown').text(`${minutes}:${seconds}`)
    },1000)
}
const mainAudio = async (name,to) => {
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                    fetch(`https://ecoles.yesbanana.org/sendMail/${to}/${conference.id}`).then(()=>{
                        console.log('audio call started')
                    })
                interval()
                    return VoxeetSDK.conference.join(conference, {audio:true,video:false})
                }
            )
            .then(() => {
                $('#body').preloader('remove')
                document.getElementById('audioComponent').style.display='flex'

            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }
}
const callsButton = document.getElementsByClassName('call')
const callsButtonAudio = document.getElementsByClassName('callAudio')
const callInput = document.getElementsByClassName('callInput')
const crewCall = document.getElementsByClassName('crewCall')
const removeCall = document.getElementsByClassName('removeCall')
const current=0
const call = (e)=>{
    $('#body').preloader()
    main(document.getElementById('directorFirstName').value,e.currentTarget.param)
}

const callAudio = (e)=>{
    $('#body').preloader()

    mainAudio(document.getElementById('directorFirstName').value,e.currentTarget.param)
}
/* crew call */
let users= []
let usersWithUsername=[]
let removeToTheCallList
 removeToTheCallList = (e)=>{
    if(users.indexOf(e.currentTarget.param)!==-1) {
        users.splice(users.indexOf(e.currentTarget.param), 1)
        usersWithUsername.splice(usersWithUsername.indexOf(e.currentTarget.username),1)
        console.log(usersWithUsername)
        crewCall[e.currentTarget.indice].style.display="inline"
        removeCall[e.currentTarget.indice].style.display="none"
         }
}

let addToTheCallList
 addToTheCallList = (e)=>{
    if(users.indexOf(e.currentTarget.param)==-1)
    {
        usersWithUsername.push(e.currentTarget.username)
        users.push(e.currentTarget.param)
        crewCall[e.currentTarget.indice].style.display="none"
        removeCall[e.currentTarget.indice].style.display="inline"
        console.log(usersWithUsername)
    }

}
const crewCallButton = document.getElementById("crewCallButton")

const crewCallButtonAudio = document.getElementById("crewCallButtonAudio")
crewCallButtonAudio.addEventListener('click',async ()=>{
    const name = document.getElementById('directorFirstName').value
    $('#body').preloader()
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                users.map(async v=> await fetch(`https://ecoles.yesbanana.org/sendMail/${v}/${conference.id}`)
                )
                return VoxeetSDK.conference.join(conference, {audio:true,video:false})
            }).then(() => {
            $('#body').preloader('remove')
            interval()
            document.getElementById('audioComponent').style.display='flex'

            usersWithUsername.map((v,i)=>$("#audioContainer").append(`<p class="font-weight-bold alert btn-success text-white text-center">${crewCallAudio[i].value}</p>`))

        })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }

})
crewCallButton.addEventListener('click',async ()=>{
    const name = document.getElementById('directorFirstName').value
    $('#body').preloader()
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                  users.map(async v=> await fetch(`https://ecoles.yesbanana.org/sendMail/${v}/${conference.id}`)
                  )
                return VoxeetSDK.conference.join(conference, {})
                    }).then(() => {

                VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).then(
                    ()=>{
                        $('#body').preloader('remove')
                        document.getElementById('video-super-container').style.display='flex'
                    }
                ).catch(e=>{console.log(e);$('#body').preloader('remove')})



            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }

})
/* crew call */
const crewCallAudio = document.getElementsByClassName("crewCallAudio")
for(i=0;i<=callInput.length-1;i++){
    callsButton[i].param = callInput[i].value
    callsButton[i].addEventListener('click',call)
    callsButtonAudio[i].param = callInput[i].value
    callsButtonAudio[i].addEventListener('click',callAudio)
    /* crew call */
    crewCall[i].param = callInput[i].value
    crewCall[i].indice = i
    crewCall[i].username= crewCallAudio[i].value
    crewCall[i].addEventListener('click',addToTheCallList)
    removeCall[i].param = callInput[i].value
    removeCall[i].indice = i
    removeCall[i].username= crewCallAudio[i].value
    removeCall[i].addEventListener('click',removeToTheCallList)
    removeCall[i].style.display="none"
    /* crew call */

}



