import React from 'react'
import '../App.css';
import Header from '../Fixed/Header'
import { useNavigate } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import { createWord } from '../redux/modules/word';
import { useForm } from 'react-hook-form'; 
import styled from "styled-components";


const Detail = () => {
  const dispatch = useDispatch();
  const inputWord = React.useRef(null);
  const inputExpress = React.useRef(null);
  const inputMeaning = React.useRef(null);
  const inputExample = React.useRef(null);
  const inputExplain = React.useRef(null);


  const navigate = useNavigate();
  const { handleSubmit, register } = useForm();
  const addWordList = () => {
    dispatch(createWord(
        {   
            word: inputWord.current.value,
            express: inputExpress.current.value,
            meaning: inputMeaning.current.value,
            example: inputExample.current.value,
            explain: inputExplain.current.value,
            completed: false
        }
    ));
    navigate("/");
  }
  return (
    <div className='App'>
    <Header />
    <h3 className='title'>단어 추가하기</h3>
      <div className='section'>
    <div>단어</div>
    <input required type="text" ref={inputWord}></input>
    <div>병음</div>
    <input required type="text" ref={inputExpress}></input>
    <div>의미</div>
    <input required type="text" ref={inputMeaning}></input>
    <div>예문</div>
    <input required type="text" ref={inputExample}></input>
    <div>해석</div>
    <input required type="text" ref={inputExplain}></input>
    </div>
    <div><button className='mybtn' onClick={addWordList}>저장하기</button></div>
    </div>
  )
}

export default Detail