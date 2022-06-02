// widgets.js

// Actions 액션 타입들을 정해주는 
// const LOAD   = 'word/LOAD';
const CREATE = 'word/CREATE'; //추가하기
const COMPLETED = 'word/COMPLETED';
const DELETE = 'word/DELETE';
// const UPDATE = 'my-app/widgets/UPDATE';
// const REMOVE = 'my-app/widgets/REMOVE';

const initialState = {
  // is_loaded: false,
  list :  [
    {
      word: "집에 가고싶다",
      express: "지베 가고 십따",
      meaning: "하기 싫다",
      example:"집에 가고싶다" ,
      explain:"집에 있는데도 집에 가고 싶다." ,
      completed: false
    }
  ],
}
let id = 0;
// Action Creators
// export function loadWord(word_list) {
//   return { type: LOAD, word_list };
// }
export function createWord(word){
  console.log("액션을 생성할거야!")
  console.log({id})
  return {type : CREATE, word: word, id:id++}
}
export function completedWord(id) {
  return { type: COMPLETED, id };
}
export function deleteWord(id) {
  return { type: DELETE, id };
}


// Reducer
export default function reducer(state = initialState, action = {}) {
  switch (action.type) {
    // case "words/LOAD":
    //   return { list: action.word_list, is_loaded: true };

    case "word/CREATE":{
      console.log("이제값을 추가할거야!")
      const new_word_list =[...state.list, action.word]
      return {list : new_word_list}
    }
    case "words/COMPLETED": {
      console.log("이제값을 완료할거야!")
      const new_word_list =state.list.map((l)=>{
        console.log(l);
        if(action.id === l.id){
          return {...l, completed:true}
        }else{
          return l
        }
      })
      return {list: new_word_list};
  }
    case "word/DELETE": {
      const new_word_list = state.word.filter((l) => {
        return action.id !== l.id;
      });
     return {list: new_word_list};
    }
    default: return state;
  }
}