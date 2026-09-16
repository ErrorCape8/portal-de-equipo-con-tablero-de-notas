exports.handler = async (event) => {
  const notes = Array.isArray(event.notes) ? event.notes : [];

  return {
    total: notes.length,
    pending: notes.filter(note => note.status === 'PENDIENTE').length,
    progress: notes.filter(note => note.status === 'EN_PROCESO').length,
    done: notes.filter(note => note.status === 'COMPLETADO').length
  };
};