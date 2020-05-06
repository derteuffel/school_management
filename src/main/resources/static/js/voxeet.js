const removeVideoNode = (participant) => {
    let videoNode = document.getElementById('video-' + participant.id);

    if (videoNode) {
        videoNode.parentNode.removeChild(videoNode);
    }
}
const main = async (name,to) => {
    console.log(to)
    VoxeetSDK.initialize('N2wzZXJrdG1zcTc3cQ==', 'NzRqZ2pocGNmdmNxa2Q5YjZob2FoYWQ0MzU=')
    VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
        addVideoNode(participant, stream);
    })
    VoxeetSDK.conference.on('streamRemoved', (participant) => {
        removeVideoNode(participant);
    });

    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                    fetch(`http://localhost:8080/sendMail/${to}/${conference.id}`).then(()=>{
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
const callsButton = document.getElementsByClassName('call')
const callInput = document.getElementsByClassName('callInput')
const current=0
const call = (e)=>{
    $('#body').preloader()
    main(document.getElementById('directorFirstName').value,e.currentTarget.param)
}
for(i=0;i<=callInput.length-1;i++){
    callsButton[i].param = callInput[i].value
    callsButton[i].addEventListener('click',call)
}


