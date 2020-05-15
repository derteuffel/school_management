document.getElementById('video-super-container').style.display = "none"
const addVideoNode = (participant, stream) => {
    const videoContainer = document.getElementById('video-container');
    let videoNode = document.getElementById('video-' + participant.id);

    if(!videoNode) {
        videoNode = document.createElement('video');

        videoNode.setAttribute('id', 'video-' + participant.id);
        
        videoNode.setAttribute('class','col-md-6 col-12');
        videoNode.setAttribute('width', '100%');
        videoNode.setAttribute('margin-bottom','16px')
        videoNode.setAttribute('padding','16px')
        videoNode.style.borderRadius="16px"
        videoContainer.appendChild(videoNode);

        videoNode.autoplay = 'autoplay';
        videoNode.muted = true;
    }

    navigator.attachMediaStream(videoNode, stream);
}

const stopVideoBtn = document.getElementById("stop")
stopVideoBtn.onclick = () => {
    const videoContainer = document.getElementById('video-container')
    VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant)
        .then(() => {
            document.getElementById('video-super-container').style.display = "none"
        })
    VoxeetSDK.conference.leave()
        .then(() => {
            document.getElementById('video-super-container').style.display = "none"
        })
        .catch((err) => {
            document.getElementById('video-super-container').style.display = "none"
            console.log(err);
        })
    VoxeetSDK.session.close()
}
const mute = ()=>{
    const opacity =  $("#mute").css('opacity')
    if(opacity==1) {
        $("#mute").css('opacity', 0.5)
        VoxeetSDK.conference.mute(VoxeetSDK.session.participant, true).catch(error => {
            console.log(error)
        })
    }
    else {
        $("#mute").css('opacity', 1)
        VoxeetSDK.conference.mute(VoxeetSDK.session.participant, false).catch(error => {
            console.log(error)
        })
    }
}
const muteAudio = ()=>{
    const opacity =  $("#muteAudio").css('opacity')
    if(opacity==1) {
        $("#muteAudio").css('opacity', 0.5)
        VoxeetSDK.conference.mute(VoxeetSDK.session.participant, true).catch(error => {
            console.log(error)
        })
    }
    else {
        $("#muteAudio").css('opacity', 1)
        VoxeetSDK.conference.mute(VoxeetSDK.session.participant, false).catch(error => {
            console.log(error)
        })
    }
}
const stopAudio = () => {
    const videoContainer = document.getElementById('audioComponent')
    const audioContainer = document.getElementById('audioContainer')

    VoxeetSDK.conference.leave()
        .then(() => {
            videoContainer.style.display = "none"
            audioContainer.style.display = "none"
        })
        .catch((err) => {
            videoContainer.style.display = "none"
            audioContainer.style.display = "none"
            console.log(err);
        })
    VoxeetSDK.session.close()
}
$("#mute").click(mute)
$("#muteAudio").click(muteAudio)
$("#stopAudio").click(stopAudio)
$("#videoOff").click(()=>{
    const opacity =  $("#videoOff").css('opacity')
    if(opacity==1) {
        $("#videoOff").css('opacity', 0.5)
        VoxeetSDK.conference.stopVideo(VoxeetSDK.session.participant).catch(error => {
            console.log(error)
        })
    }
    else {
        $("#videoOff").css('opacity', 1)
        VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).catch(error => {
            console.log(error)
        })
    }
})
